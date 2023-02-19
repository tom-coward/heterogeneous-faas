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
            List<string> sumList = new List<string>();
            string str;
            while ((str = Console.ReadLine()) != null)
            {
                string[] str1 = str.Split(' ');
                int a = int.Parse(str1[0]);
                int b = int.Parse(str1[1]);
                sumList.Add((a + b).ToString());
            }
            for (int i = 0; i < sumList.Count; ++i)
            {
                Console.WriteLine(Measure(sumList, i));
            }

        }
        static int Measure(List<string> sumList,int i)
        {
            return sumList[i].Length;
        }

    }
}

