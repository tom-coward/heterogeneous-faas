using System;

class Program {
    static void Main() {
        string line;
        while ((line = Console.ReadLine()) != null) {
            var l = line.Split(' ');
            var sum = int.Parse(l[0]) + int.Parse(l[1]);
            var n = Math.Truncate(Math.Log10(Math.Max(sum, 1))) + 1;
            Console.WriteLine(n);
        }
    }
}