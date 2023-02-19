using System;

class WhileSample
{
    static void Main()
    {

        while (true)
        {
            string input = Console.ReadLine();
            if (input == null) return;
            string[] input_array = input.Split(' ');

            int a = int.Parse(input_array[0]);
            int b = int.Parse(input_array[1]);

            int sum = a + b;

            Console.WriteLine(sum.ToString().Length);

        }
    }
}

