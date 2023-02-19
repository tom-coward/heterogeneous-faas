using System;
using System.Collections.Generic;

class Program
{
    public static void Main (string[] args)
    {
        String _str;
        String[] str;
        while ((_str = Console.ReadLine()) != null
            && (str = _str.Split(' ')).Length == 2) {
            int digit = 1,
                left = int.Parse (str [0]),
                right = int.Parse (str [1]);
            for (uint tmp = (uint)(left + right); tmp / 10 > 0; tmp /= 10)
                ++digit;
            System.Console.WriteLine (digit);
         }
    }
}