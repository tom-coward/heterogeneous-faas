using System;
using System.Collections.Generic;
using System.Linq;
namespace csharptest
{
    class Program
    {
        static void Main(string[] args)
        {
            int i = 0;
            int[] ans = new int[200];
            while (true)
            { 
                string str = Console.ReadLine();
                if (str == null)
                {
                    break;
                }
                else
                {
                    string[] s = str.Split(' ');
                    int a = int.Parse(s[0]);
                    int b = int.Parse(s[1]);
                    a += b;
                    ans[i] = a.ToString().Length;
                    i++;
                }
            }
            for (int n = 0; n < i; n++)
            {
                Console.WriteLine(ans[n]);
            }
        }
    }
}