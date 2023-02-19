using System;
using System.Linq;
using System.IO;
using System.Text;
using System.Collections;
using System.Collections.Generic;
 
class Program{
    static void Main(){
        var ans = new List<int>();
        string[] inputs;
        while(true)
        {
            string input = Console.ReadLine();
            if(string.IsNullOrEmpty(input)) break;
            inputs = input.Split(' ');
            ans.Add(DigitNumber(int.Parse(inputs[0]) + int.Parse(inputs[1])));
        }

        foreach (int val in ans)
        {
            Console.WriteLine(val);
        }
    }

    static bool IsNullOrEmpty(Array array){
        return array == null || array.Length == 0;
    }

    static int DigitNumber(int num){
        int cnt = 0;
        do
        {
            num /= 10;
            cnt++;
        } while (num > 0);
        return cnt;
    }
}