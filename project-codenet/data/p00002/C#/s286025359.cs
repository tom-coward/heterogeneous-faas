using System;
using System.Collections.Generic;
using System.Linq;

namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {
            int a, b;
            string tmp;
            string[] inp;
            List<int> res = new List<int>();

            while (true)
            {
                tmp = Console.ReadLine();
                if (tmp == null)
                {
                    break;
                }
                inp = tmp.Split(' ');
                a = int.Parse(inp[0]);
                b = int.Parse(inp[1]);

                int c = a + b;

                int keta = 1;
                while (c > 9)
                {
                    c = c / 10;
                    keta++;
                }
                res.Add(keta);
            }
            for (int i = 0; i < res.Count; i++)
            {
                Console.WriteLine(res[i]);
            }
        }
    }
}