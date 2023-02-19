using System;

namespace AOJ
{
    class DigitNumber
    {
        public static void Main(string[] args)
        {
            for (int i = 0; i < 200; i++)
            {
                string input = Console.ReadLine();
                if (string.IsNullOrEmpty(input)) { break; }

                var tmp = input.Split(' ');

                int test = int.Parse(tmp[0]) + int.Parse(tmp[1]);
                Console.WriteLine(test.ToString().Length);
            }
        }
    }
}