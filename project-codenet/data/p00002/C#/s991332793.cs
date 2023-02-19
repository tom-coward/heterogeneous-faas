using System;
using System.Collections.Generic;

class Program
{
    static void Main(string[] args)
    {
        List<int[]> list = new List<int[]>();

        while (true) {
            string v = Console.ReadLine();
            if (string.IsNullOrEmpty(v) == true) { break; }

            int a = int.Parse(v.Split(' ')[0]);
            int b = int.Parse(v.Split(' ')[1]);
            list.Add(new int[] { a, b });
        }

        for (int i = 0; i < list.Count; i++) {
            int c = list[i][0] + list[i][1];
            Console.WriteLine(c.ToString().Length);
        }
    }
}