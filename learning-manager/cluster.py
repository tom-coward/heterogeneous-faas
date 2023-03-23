from cassandra.cluster import Cluster
import cProfile
import io
import pstats
from sklearn.cluster import KMeans
import pickle
import aiohttp
import json
import time
import asyncio
import numpy

cassandraCluster = Cluster(['localhost'])
cassandraSession = cassandraCluster.connect()

resourceManagerUrl = "http://localhost:5001"


def getFunctions():
    results = cassandraSession.execute(f"SELECT name, source_code, example_inputs FROM heterogeneous_faas.function")

    return results.all()

async def executeFunction(functionName: str, worker: str, exampleInputs: list, inputSize: int):
    inputs = exampleInputs[:inputSize]
    inputsString = ", ".join(inputs)
    inputsJson = json.dumps(inputsString)
    
    requestBody = "{\"function_name\": \"" + functionName + "\", \"worker\": \"" + worker + "\", \"function_payload\": " + inputsJson + "}"
    data = bytes(requestBody, encoding='utf-8')

    async with aiohttp.ClientSession() as session:
        requestStartTime = time.time()

        async with session.put(f"{resourceManagerUrl}/function/invoke", data=data) as response:
            response = await response.text()

            requestEndTime = time.time()
            requestTime = (requestEndTime - requestStartTime) * 1000 # get request duration in ms

    print(f"Request took {requestTime} seconds. Response: {response}")
    return requestTime
        

def profileFunction(sourceCode: str, exampleInputs: list):
    selectedInputs = exampleInputs[0]

    profiler = cProfile.Profile()

    compiledCode = compile(f"{sourceCode}\nprofiler.enable()\nhandler({selectedInputs}, None)\nprofiler.disable()", "main.py", "exec")

    exec(compiledCode)

    profilerOutput = io.StringIO()
    profilerStats = pstats.Stats(profiler, stream=profilerOutput).sort_stats("cumulative")
    profilerStats.print_stats()

    profilerOutput.seek(0)
    stats = profilerOutput.read()

    return stats

def fitCluster(x):
    n_clusters = min(len(x), 8) # max 8 clusters - but can only have as many clusters as functions

    cluster = KMeans(n_clusters=n_clusters).fit(x)

    return cluster

def saveCluster(clusterBytes):
    saveClusterStatement = cassandraSession.prepare(f"INSERT INTO heterogeneous_faas.cluster (id, model) VALUES (?, ?)")
    cassandraSession.execute(saveClusterStatement, ("cluster", clusterBytes))

def getCluster():
    result = cassandraSession.execute(f"SELECT model FROM heterogeneous_faas.cluster WHERE id='cluster'")

    return result.one().model

async def cluster():
    functions = getFunctions()

    x = []

    for function in functions:
        functionName = function.name
        sourceCode = function.source_code
        exampleInputs = function.example_inputs

        #profileStats = profileFunction(sourceCode, exampleInputs)

        #print(profileStats)

        # execute once on cloud & edge to avoid cold start
        await executeFunction(functionName, 'AWS', exampleInputs, 1)
        await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1)

        executionCloud1 = await executeFunction(functionName, 'AWS', exampleInputs, 1)
        executionEdge1 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1)
        executionCloud100 = await executeFunction(functionName, 'AWS', exampleInputs, 100)
        executionEdge100 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 100)
        executionCloud1000 = await executeFunction(functionName, 'AWS', exampleInputs, 1000)
        executionEdge1000 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1000)
        executionCloud1500 = await executeFunction(functionName, 'AWS', exampleInputs, 1500)
        executionEdge1500 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 1500)
        executionCloud2000 = await executeFunction(functionName, 'AWS', exampleInputs, 2000)
        executionEdge2000 = await executeFunction(functionName, 'KUBERNETES', exampleInputs, 2000)

        x.append([executionCloud1, executionEdge1, executionCloud100, executionEdge100, executionCloud1000, executionEdge1000, executionCloud1500, executionEdge1500, executionCloud2000, executionEdge2000])

    cluster = fitCluster(x)

    clusterBytes = pickle.dumps(cluster)
    saveCluster(clusterBytes)

    return cluster

def predict(x):
    clusterBytes = getCluster()
    cluster = pickle.loads(clusterBytes)

    predictedCluster = cluster.predict(x)

    distances = cluster.transform(x)
    distanceThreshold = 0.5

    print(distances[0][predictedCluster[0]])

    #if distances[0][predictedCluster[0]] > distanceThreshold:
        #raise Exception("Prediction is too far from cluster")

    return predictedCluster[0]


if __name__ == '__main__':
    cluster = asyncio.run(cluster())

    functionName = "s103878717"
    exampleInputs = ["37646 576251", "62339 543159", "264301 344086", "144024 279188", "639392 143521", "478732 724823", "918656 583988", "702833 245296", "389120 824366", "444818 185632", "776680 771968", "412163 589065", "123675 40242", "771879 866520", "283551 275394", "332641 455519", "537395 411524", "428969 777672", "180774 967796", "479997 234292", "526476 654505", "758811 803812", "238860 285862", "171625 28676", "31970 663322", "270933 531125", "97742 179692", "473842 297490", "934464 857699", "35020 94671", "840006 603073", "753669 816704", "459482 105481", "460892 482614", "305523 78894", "320795 378284", "121585 631277", "148693 161377", "248875 166939", "321228 201203", "419636 792578", "30442 458258", "968513 143536", "634298 720155", "840997 734761", "143970 258848", "266496 499520", "657609 695999", "803688 351324", "544310 538877", "16408 731124", "21773 464973", "786484 242239", "229742 95352", "362139 146045", "245874 735227", "76264 281186", "883058 214295", "876954 468372", "837787 903540", "933851 634828", "118106 814548", "6405 591106", "796938 997076", "125077 135436", "774304 20445", "484539 235840", "210118 996478", "494594 601439", "663702 429000", "3633 863938", "408941 998946", "664686 988589", "676819 788984", "55929 651026", "103564 865750", "760005 737038", "154784 970518", "699917 990394", "64849 288076", "995896 625306", "849758 808895", "343552 843731", "947335 155015", "306118 912790", "976333 540962", "318481 883198", "604043 660213", "857317 626948", "172990 930857", "24903 49319", "348959 956096", "660976 306470", "882436 666515", "153363 119883", "158433 937886", "109951 214155", "211876 185584", "448304 971076", "931706 437163", "677218 768017", "764915 700446", "637265 841194", "323791 952657", "232916 475011", "881412 809153", "629653 162199", "558698 668918", "357770 63812", "84053 285982", "29735 414109", "798748 955786", "233637 731316", "968067 507268", "196205 728830", "696717 22262", "55431 608038", "324443 807838", "51906 65148", "368369 648208", "801014 168191", "128460 151581", "824625 586641", "522673 148516", "612946 709935", "535343 618457", "661476 788003", "445735 536884", "521665 615536", "220274 50063", "782083 498641", "191155 625534", "244711 290183", "428234 965239", "668930 69056", "348340 957446", "883382 323618", "407567 193284", "340265 613155", "295655 577380", "633591 231830", "41420 850143", "38338 395720", "957520 70499", "570931 974880", "745101 323632", "385134 792524", "597891 227184", "255396 454169", "257890 464007", "520326 654437", "558623 773728", "494528 245165", "111430 546167", "354148 706081", "376086 346171", "386601 857512", "428708 782135", "55035 78401", "807914 848840", "33891 72594", "759437 289104", "35239 653112", "194709 959729", "511561 707824", "974960 542322", "615914 392591", "137624 271822", "200197 197300", "177112 263808", "681466 543237", "645300 138223", "726850 124858", "779675 864703", "807253 792457", "373075 915932", "928655 488898", "645806 980926", "77566 652723", "895959 366616", "680400 754768", "771217 109595", "240428 825304", "433783 103147", "151537 720583", "678680 22754", "361927 702294", "945558 427684", "101646 172229", "906342 397977", "42624 461973", "406915 888291", "996997 340857", "944965 558548", "119521 97747", "469554 954528", "521047 813585", "442293 587667", "19752 256873", "440823 855017", "232999 91356", "647380 456214", "180945 215768", "73658 395070", "918115 923481", "546632 306036", "338533 913243", "371709 502782", "146585 254238", "656253 803280", "356566 556455", "996755 993884", "878033 705409", "772603 73952", "222403 814844", "442007 661103", "713864 958636", "493489 50721", "985691 235443", "325798 718555", "505249 700839", "859054 476516", "182105 553380", "674124 250665", "368472 1778", "168805 188175", "981896 437622", "86415 675025", "984125 510834", "790645 57070", "377924 617167", "755119 973264", "864673 95114", "122835 336985", "301903 785840", "375877 750050", "808705 43312", "782467 67943", "821110 104390", "611146 886485", "504544 587963", "966098 650127", "234489 231201", "943011 441866", "199899 94980", "124993 78624", "980168 70212", "720676 670819", "385491 860214", "850293 441782", "453582 926257", "880246 294182", "912186 889369", "482217 176462", "433276 633543", "232866 106534", "551175 165764", "655106 734877", "851746 342625", "635721 802068", "723394 80009", "304088 264726", "670920 9239", "336418 925451", "751179 531896", "373486 829026", "278352 549017", "636843 518749", "766833 819599", "889307 138687", "574677 200603", "62583 74416", "883306 510950", "379525 521584", "609238 959751", "61677 563624", "155200 742198", "228645 875768", "711246 303358", "412831 850279", "873798 351952", "232329 181537", "119428 402943", "785235 407086", "964951 111551", "386334 252", "304479 539827", "601446 952050", "372340 549602", "457325 583409", "296367 488736", "92594 824276", "555745 135510", "741726 25824", "744376 449404", "164622 25435", "886753 48209", "927291 259575", "546006 211582", "447295 527719", "393044 576890", "621298 494200", "734781 851237", "40164 86030", "538928 549815", "135293 310246", "265979 37584", "920423 423057", "146155 628452", "944619 405105", "847564 264881", "145833 692632", "919916 172901", "238148 759479", "596337 139671", "708116 468756", "581706 730362", "799532 418355", "530705 435598", "571360 34305", "592865 636067", "871657 837482", "260391 244165", "424252 773033", "681681 864459", "283897 688475", "343763 580116", "368543 410778", "663113 498076", "284516 58009", "719108 797155", "53670 328562", "20249 257045", "331422 952375", "647758 31936", "173038 167097", "529311 345949", "375994 341201", "637370 583107", "289300 633606", "267707 922929", "517054 281152", "268106 135038", "848299 57577", "618306 116681", "21188 943001", "506694 503653", "18409 313017", "111927 33938", "469524 115030", "110794 784296", "331539 717187", "521215 224784", "844099 899284", "987338 985577", "936634 962595", "671094 875900", "356479 551060", "280349 71295", "315541 142476", "520540 611592", "407651 394344", "156994 493240", "388540 241099", "595348 173007", "709093 680654", "939495 609685", "359636 715140", "518561 320841", "921976 483363", "755931 625510", "778968 6032", "102204 644388", "272390 777748", "933818 673752", "705327 348437", "956388 613468", "942438 823692", "291081 429084", "135898 383345", "501626 894704", "147682 937849", "559244 842687", "205850 371036", "849334 126400", "862372 855240", "570757 775225", "146918 105655", "821681 521157", "941143 574826", "796980 392258", "885421 803463", "197685 789324", "248864 490041", "537562 183313", "791006 54607", "619360 270431", "173228 709983", "220049 643255", "74379 6232", "74088 415823", "677014 374951", "806986 399460", "713192 540605", "615642 793658", "462345 347981", "44128 124813", "253173 569667", "599913 199361", "831380 311203", "902427 402850", "360655 289339", "569090 159893", "823806 315221", "269723 460827", "602715 462964", "559097 695931", "311043 793363", "402008 248381", "963945 245822", "146082 240358", "149857 621876", "797667 862921", "434912 924922", "333117 155903", "880230 816918", "260139 48555", "716112 240848", "657209 936134", "379708 330521", "981936 859834", "400857 992543", "565087 76856", "542744 880080", "659394 587015", "76365 700703", "797993 59804", "150200 844108", "71353 130243", "542846 555081", "673305 751180", "494209 358904", "339164 574940", "871357 979607", "400972 1886", "831272 57319", "684338 546365", "732713 954524", "172555 629303", "733301 982991", "883516 98208", "145259 911848", "794232 707520", "984804 1389", "956709 942374", "823749 568014", "808281 736345", "362904 770266", "472347 190662", "683734 456428", "44377 807090", "635249 453941", "125778 516981", "314551 649690", "585913 399986", "427021 791325", "262369 90645", "648216 320292", "102939 389290", "703221 306183", "605046 66260", "888993 698608", "10315 260505", "625929 249974", "229634 624165", "757370 808230", "119384 58025", "738540 674410", "886924 406841", "172630 771361", "487946 14201", "193109 311327", "11620 753068", "593991 718018", "984008 500723", "322385 126017", "428390 300627", "264118 459587", "687464 597942", "976268 419840", "309354 922113", "410244 162967", "428734 868799", "441722 463340", "491732 515023", "720602 138031", "783574 627611", "763966 508176", "25701 993611", "921013 858814", "329073 16001", "829880 619058", "300059 123521", "129132 791890", "405873 213218", "482103 932431", "658831 235368", "356636 263686", "101922 656380", "985033 230522", "582324 204647", "728188 969815", "77124 142746", "869719 384480", "869302 101531", "548097 938906", "104671 454627", "324298 146470", "761659 560606", "140811 508667", "491437 154705", "128658 326764", "932434 989907", "679227 353094", "245900 761470", "749778 356861", "656971 604719", "875708 765468", "677038 675339", "783053 735296", "928085 807106", "417080 556241", "275765 726835", "113160 176243", "333067 649669", "234330 239523", "757387 356652", "338692 694216", "412140 358511", "502036 865581", "635536 617509", "368513 719530", "959903 565901", "792194 414762", "40480 158854", "713614 210098", "889699 463729", "782986 993642", "635585 709182", "853044 461503", "978391 953306", "774294 398576", "12669 457801", "515661 72448", "956028 634824", "416929 688224", "711641 493025", "871598 834708", "661571 288278", "551220 859116", "743298 375607", "336885 203269", "215918 309390", "7546 991140", "916940 793863", "609794 963643", "365236 207284", "539206 735252", "670404 781429", "574157 401709", "524224 742615", "198463 612349", "183959 323624", "871775 979940", "40172 423860", "44766 240933", "127800 685522", "618616 31873", "191456 553986", "908735 447573", "442271 674517", "803631 664389", "333850 639345", "113528 85224", "848963 473737", "28235 418099", "817244 759481", "701718 861768", "586371 6296", "888438 320199", "539346 526291", "959666 382686", "373367 607302", "749313 132842", "270320 184151", "380598 389446", "951833 670613", "300429 147111", "136692 721533", "446129 363121", "381749 859798", "365111 62571", "539659 684220", "449407 104289", "512911 480968", "669554 960086", "849586 526164", "774385 915525", "706334 797550", "644412 914803", "286923 14239", "733368 263064", "187285 796860", "233715 623997", "463051 307762", "443033 651446", "632739 436751", "2034 389039", "970843 983461", "288745 682230", "550530 769114", "617456 769735", "831009 110357", "326323 436202", "160527 17900", "877094 591277", "653079 825378", "840000 173047", "710883 503602", "88545 809900", "179471 314913", "81323 876863", "778719 951221", "107537 110678", "578013 205312", "511612 468115", "611432 838223", "650570 394814", "55993 752836", "749063 87613", "85600 143104", "761795 356995", "390018 331824", "661063 892417", "417244 258451", "930297 69990", "386404 971846", "984172 809818", "803873 277267", "693758 129753", "92740 622490", "666534 339969", "934359 366345", "62796 362116", "294148 808171", "940107 597721", "597390 911009", "602324 84968", "117760 196995", "232289 90020", "456230 679182", "266569 451566", "759801 642574", "769352 166495", "607924 771490", "603492 595026", "292633 608783", "133492 578471", "275913 466698", "398490 463542", "869629 909390", "863132 397148", "435393 625511", "331835 300634", "857313 39773", "281819 626719", "247946 663139", "211779 58903", "992244 362447", "783872 739595", "962599 394829", "669669 899824", "489709 10342", "686409 405874", "406294 412144", "821665 908372", "281646 419228", "540021 22108", "26485 807681", "951679 85743", "345115 226227", "739396 505431", "601046 915800", "339653 767866", "573035 59551", "933098 126733", "761958 854082", "447483 754297", "129316 149348", "259470 372734", "970850 664209", "884222 687511", "427318 457046", "793372 900151", "712533 876354", "411239 257340", "632929 235365", "925396 709496", "498413 516301", "182624 17692", "674483 860133", "991865 489096", "575037 495187", "68717 672647", "969900 88669", "98823 133578", "991267 886647", "813015 936446", "776524 609041", "309433 383557", "805766 387275", "227682 632692", "912530 214242", "527876 853517", "666034 304682", "66496 967465", "261470 400949", "781587 563390", "151724 852009", "446596 505434", "81912 765849", "225497 136840", "2092 35758", "628428 433116", "524457 954934", "573954 836635", "407683 366725", "732617 233777", "72410 850739", "293171 34746", "296793 353683", "143555 885841", "788286 320403", "221075 222287", "913268 155654", "284506 278266", "357353 993742", "419687 317303", "286756 895235", "441176 210972", "981364 748587", "824754 532030", "470469 515992", "904520 263111", "628229 489265", "613824 777903", "144931 803949", "467186 519234", "809341 166609", "409405 429614", "787163 874949", "999678 279525", "718134 752685", "628319 666174", "130249 266459", "502465 7998", "119207 969464", "864749 318280", "684029 165600", "229990 951293", "213465 61159", "187316 356808", "775037 344431", "352854 749308", "901295 719", "905739 765211", "617567 941407", "822678 377165", "684666 910887", "707567 593873", "937421 17046", "18871 850267", "562137 691042", "869743 61882", "196736 67334", "43655 182854", "346146 458243", "144272 539706", "495858 530604", "896422 394756", "378743 955911", "893225 306426", "945317 436519", "115741 322517", "206857 242565", "160613 879430", "757856 984560", "453741 322292", "163341 776067", "852516 447805", "458770 793949", "110130 15828", "83172 204241", "798024 172816", "77514 39485", "643435 493143", "325868 902417", "407826 133901", "632718 248451", "960797 448901", "88405 471264", "632839 606750", "361586 344996", "667862 915108", "2327 378801", "287585 582713", "893169 116962", "134279 730514", "36515 66035", "574170 119035", "561186 320765", "439312 741126", "621266 235515", "156672 884522", "86045 335657", "482325 261673", "418681 7849", "910263 271065", "691323 774844", "959090 465697", "347675 940308", "916484 147139", "893061 930590", "89034 713571", "575920 572086", "59021 163052", "901905 129960", "603629 692453", "645295 181214", "206778 292407", "18492 827703", "508258 31599", "722301 744247", "838518 210667", "792187 698133", "877417 285213", "964339 907075", "844041 94933", "858533 311852", "395632 305458", "20210 847360", "570852 889386", "731784 692961", "913782 901368", "856623 56880", "577047 999552", "246483 690893", "580880 89391", "108400 397471", "206028 102147", "327623 549212", "597535 908724", "537227 433477", "620796 442871", "384069 23973", "641720 586805", "101968 420192", "465256 320591", "718565 923515", "359147 718583", "39452 893565", "999608 75742", "162959 31688", "458426 180396", "617014 163112", "800867 773335", "135287 791664", "146879 671705", "107341 493372", "528306 47390", "294034 596081", "391763 326115", "162357 791126", "231436 117708", "202825 162916", "964936 205725", "571225 282768", "59538 925679", "663211 473921", "49598 517539", "855025 898063", "699385 932555", "238227 17464", "638190 840204", "45776 21882", "363900 845384", "766974 679458", "526660 184659", "692708 750649", "168403 608918", "568481 805044", "195644 922746", "100319 948823", "280018 333310", "164475 340626", "65331 419518", "227501 111642", "903465 616268", "736760 490059", "290637 175025", "688793 224752", "481092 681580", "262229 12449", "30712 385048", "236336 160005", "850147 303369", "97461 32915", "905521 294086", "992844 919454", "770020 975054", "151699 926776", "710057 934096", "336413 493422", "535557 519318", "843074 632784", "855091 122630", "735028 37099", "257337 20925", "852476 664437", "308832 614490", "604450 779964", "40504 304505", "873128 719802", "256923 247746", "636860 780843", "172834 632380", "304655 479186", "988983 655912", "97358 303130", "552764 669875", "565501 507146", "849055 769888", "282035 463989", "297462 1087", "181153 783893", "185118 405000", "803531 917533", "501141 680704", "226711 698594", "810719 798206", "638204 934660", "373883 512018", "510820 951307", "965063 869161", "412880 143656", "232171 624340", "563766 283576", "494330 236879", "30034 866371", "125334 898416", "664652 634932", "55214 788066", "870964 458036", "794167 389618", "429066 890258", "304979 578415", "425214 689234", "636488 905289", "817805 353364", "416037 599100", "721171 171412", "408897 310394", "740965 729014", "452382 576010", "347590 290426", "227414 889524", "982746 945656", "956566 273065", "737558 756071", "905678 852366", "669125 690591", "67695 74548", "905200 544676", "940987 747272", "924239 214392", "67441 110454", "68160 863806", "714500 577522", "705991 323656", "397887 914750", "270047 953011", "470696 908152", "252195 840352", "155449 73806", "383297 935486", "272165 491134", "982223 519658", "61557 855262", "775771 412582", "702699 669773"]

    executionCloud1 = asyncio.run(executeFunction(functionName, 'AWS', exampleInputs, 1))
    executionEdge1 = asyncio.run(executeFunction(functionName, 'KUBERNETES', exampleInputs, 1))
    executionCloud100 = asyncio.run(executeFunction(functionName, 'AWS', exampleInputs, 100))
    executionEdge100 = asyncio.run(executeFunction(functionName, 'KUBERNETES', exampleInputs, 100))
    executionCloud1000 = asyncio.run(executeFunction(functionName, 'AWS', exampleInputs, 1000))
    executionEdge1000 = asyncio.run(executeFunction(functionName, 'KUBERNETES', exampleInputs, 1000))
    executionCloud1500 = asyncio.run(executeFunction(functionName, 'AWS', exampleInputs, 1500))
    executionEdge1500 = asyncio.run(executeFunction(functionName, 'KUBERNETES', exampleInputs, 1500))
    executionCloud2000 = asyncio.run(executeFunction(functionName, 'AWS', exampleInputs, 2000))
    executionEdge2000 = asyncio.run(executeFunction(functionName, 'KUBERNETES', exampleInputs, 2000))

    x = numpy.array([executionCloud1, executionEdge1, executionCloud100, executionEdge100, executionCloud1000, executionEdge1000, executionCloud1500, executionEdge1500, executionCloud2000, executionEdge2000]).reshape(1, -1)

    print(predict(x))