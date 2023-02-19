using System;

class Class01
{
    public static void Main(string[] args)
    {
        while (true)
        {
            string s = Console.ReadLine();
            if (s == null) break;

            string[] m = s.Split(' ');
            int sum = int.Parse(m[0]) + int.Parse(m[1]);

            int count = 1;
            while (true)
            {
                sum = sum / 10;
                if (sum == 0) break;
                count++;
            }
            Console.WriteLine(count);
        }
    }
}