using System;
class latte
{
    static void Main()
    {
        while (true)
        {
            string s = Console.ReadLine();
            if (s == null) break;

            string[] t = s.Split();
            int a = int.Parse(t[0]);
            int b = int.Parse(t[1]);
            int c = a + b;
            Console.WriteLine(c.ToString().Length);
        }
    }
}