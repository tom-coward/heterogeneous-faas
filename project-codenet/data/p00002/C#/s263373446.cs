using System;
using System.Collections.Generic;
using System.Linq;

class Program {
    static void Main(string[] args) {
        string s;
        while (!string.IsNullOrEmpty(s = Console.ReadLine())) {
            string[] ss = s.Split(' ');
            int a = int.Parse(ss[0]);
            int b = int.Parse(ss[1]);
            Console.WriteLine((a + b).ToString().Length);
        }
    }
}       