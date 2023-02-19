using System;
using System.Collections.Generic;

class Program
{
    static void Main()
    {
        List<int> numbers = new List<int> { };      //int 2tu WA kakunou

        

        while (true)
        {
            string stringg = Console.ReadLine();

            if (stringg == null)
                break;

            string[] str = stringg.Split(' ');
            int x = int.Parse(str[0]);
            int y = int.Parse(str[1]);


            numbers.Add(x + y);

        }


        for ( int i=0;i < numbers.Count;i++)
        {
            int x = numbers[i];
            int digit = 0;

            while(true)
            {
                x = x / 10;

                digit += 1;

                if (x == 0)
                    break;
            }

            Console.WriteLine(digit);
        }

    }
}