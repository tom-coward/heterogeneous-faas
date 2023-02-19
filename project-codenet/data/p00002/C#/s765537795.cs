using System;
using System.Collections.Generic;
public class Hello{
    public static void Main(){
        string DataSet;
        string[] Data;
        List<int> AddLen = new List<int>();
        while((DataSet = Console.ReadLine()) != null){
            Data = DataSet.Split(' ');
            AddLen.Add((int.Parse(Data[0]) + int.Parse(Data[1])).ToString().Length);
        }
        for(int i = 0; i < AddLen.Count; i++){
            Console.WriteLine(AddLen[i]);
        }
    }
}