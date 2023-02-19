using System;
using System.Collections.Generic;
using System.Linq;

namespace AOJ.Volume0
{
    class Program0002
    {
        static void Main(string[] args)
        {
            const int N = 2;
            string input;
            Queue<int[]> queue = new Queue<int[]>();

            while ((input = Console.ReadLine()) != null)
            {
                string[] splitedInput = input.Split(' ');
                int[] pair = new int[N];

                for (int i = 0; i < N; i++)
                {
                    pair[i] = int.Parse(splitedInput[i]);
                }

                queue.Enqueue(pair);
            }

            foreach (var pair in queue)
            {
                Console.WriteLine("{0}", pair.Sum().ToString().Length);
            }
        }
    }
}