using System;
using System.Collections.Generic;

class Program
{
    //Constで定義すると警告が出るので、変数として定義
    static string InputPattern = "Input2";

    static List<string> GetInputList()
    {
        var WillReturn = new List<string>();

        if (InputPattern == "Input1") {
            WillReturn.Add("5 7");
            WillReturn.Add("1 99");
            WillReturn.Add("1000 99");
        }
        else {
            string wk;
            while ((wk = Console.ReadLine()) != null) WillReturn.Add(wk);
        }
        return WillReturn;
    }

    static void Main()
    {
        List<string> InputList = GetInputList();

        foreach (string EachStr in InputList) {
            string[] IntArr = EachStr.Split(' ');
            int wkSum = int.Parse(IntArr[0])+int.Parse(IntArr[1]);
            int Ketasuu = (int)Math.Truncate(Math.Log10(wkSum))+1;

            Console.WriteLine(Ketasuu);
        }
    }
}