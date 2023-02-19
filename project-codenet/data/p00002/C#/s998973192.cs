using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace _0002
{
    class Program
    {
        static void Main(string[] args)
        {
            string s = "";
            int[] term;
            List<int> answer = new List<int>();
            while ((s = Console.ReadLine()) != null)
            {
                term = s.Split(' ').Select(int.Parse).ToArray();
                int i = term.Sum();
                answer.Add(i.ToString().Length);
            }
            foreach (int item in answer)
            {
                Console.WriteLine(item);
            }
        }
    }
}

