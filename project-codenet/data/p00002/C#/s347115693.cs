using System;

namespace Volume0_0002_DigitNumber
{
    class Program
    {
        static void Main(string[] args)
        {
            while(true)
            {
                String input = Console.ReadLine();

                if (input == null) break;

                String[] inputs = input.Split(' ');
                int a = Convert.ToInt32( inputs[0] );
                int b = Convert.ToInt32( inputs[1] );

                Console.WriteLine((a + b).ToString().Length);
            }
        }
    }
}