using System;
using System.Linq;

class Program {
    static void Main() {
        string s;
        while ((s = Console.ReadLine()) != null) {
            int n = s.Split().Select(int.Parse).Sum().ToString().Length;
            Console.WriteLine(n);
        }
    }
}
