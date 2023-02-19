using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {
            StringBuilder sb = new StringBuilder();

            while (true)
            {
                string input = Console.ReadLine();

                if (string.IsNullOrEmpty(input)) break;

                int[] nums = Array.ConvertAll(input.Split(' '), int.Parse);
                string sum = (nums[0] + nums[1]).ToString();
                sb.AppendLine(sum.Length.ToString());
            }
            Console.Write(sb);
        }
    }
}