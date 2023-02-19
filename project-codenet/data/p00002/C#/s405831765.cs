using System;

class main {
  static void Main(){
    string str;
    while(true){
      str = Console.ReadLine();
      if(str == null) break;
      string[] strs = str.Split();
      Console.WriteLine(
        (
          Convert.ToInt32(strs[0]) + Convert.ToInt32(strs[1])
        ).ToString().Length
      );
    }
  }
}
