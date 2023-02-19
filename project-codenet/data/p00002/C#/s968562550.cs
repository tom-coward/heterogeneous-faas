using System;
using System.Text;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;



    class Program
    {
        static void Main(string[] args)
        {
            while (true)
            {
                string a;
                a = Console.ReadLine();
                if (a == null) break;
                string[] b = a.Split();
                int sum, cou = 0;
                sum = int.Parse(b[0]) + int.Parse(b[1]);
                for (; ; )
                {
                    if (sum >= 10)
                    {
                        sum = sum / 10;
                        cou++;
                    }
                    else
                    {
                        cou++;
                        break;
                    }
                }
                Console.WriteLine(cou);
            }
        }
    }