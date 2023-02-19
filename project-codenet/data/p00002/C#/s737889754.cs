using System;
using System.Collections.Generic;
using System.Linq;

class Program {
    static string ReadLine() { return Console.ReadLine(); }
    static int ReadInt() { return int.Parse(ReadLine()); }
    static int[] ReadInts() { return ReadLine().Split().Select(int.Parse).ToArray(); }
    static string[] ReadStrings() { return ReadLine().Split(); }

    static void Main() {
        string s;
        while ((s = ReadLine()) != null) {
            int n = s.Split().Select(int.Parse).ToArray().Sum();
            // Console.WriteLine(n.ToString().Length);

            int ans = (int)Math.Log10(n) + 1;
            Console.WriteLine(ans);
        }
    }
}