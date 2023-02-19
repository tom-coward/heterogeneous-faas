using System;


class AOJ
{

    public static void Main(){


        while (true)
        {

            string ss = Console.ReadLine();

            if (ss == null) break;

            string[] s = ss.Split(' ');

            int c = int.Parse(s[0]) + int.Parse(s[1]);

            int digit = 0;

            do
            {
                c /= 10;
                digit++;
            } while (c != 0);

            Console.WriteLine(digit);
        }

    }
}