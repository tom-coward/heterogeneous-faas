using System;

namespace aizuoj
{
    class Program
    {
        static void Main(string[] args)
        {
            while (true)
            {
                string input = Console.ReadLine();
                if (input == null) break;

                int a = Int32.Parse(input.Split(' ')[0]);
                int b = Int32.Parse(input.Split(' ')[1]);

                Console.WriteLine((a + b).ToString().Length);
            }
            
        }
    }
}

