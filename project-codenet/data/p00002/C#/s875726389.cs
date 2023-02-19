using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Volume0_0002
{
    class Program
    {
        static void Main(string[] args)
        {
            while (true)
            {
                string s = Console.ReadLine();
                if (s == null)
                {
                    break;
                }
                int[] Nums = s.Split(' ').Select(int.Parse).ToArray();
                string Sum = (Nums[0] + Nums[1]).ToString();
                Console.WriteLine(Sum.Length);
            }
        }
    }
}

