using System;

class AOJ {
    static void Main(string[] args)
    {
        string s;

        while((s = Console.ReadLine()) != null) {
            string[] n = s.Split(' ');

            int sum = int.Parse(n[0]) + int.Parse(n[1]);

            Console.WriteLine(sum.ToString().Length);
        }
    }
}