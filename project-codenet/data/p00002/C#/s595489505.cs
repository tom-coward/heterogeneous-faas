using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace _0003
{
    class Program
    {
        static void Main(string[] args)
        {
            // 与えられた2つの整数a,bの和の桁数を出力するプログラム

            string str;
            while ((str = Console.ReadLine()) != null)
            {
                int[] ary = str.Split(' ').Select(int.Parse).ToArray();
                int a = ary[0];
                int b = ary[1];
                Console.WriteLine((a + b).ToString().Length);
            }
        }
    }
}

