using System;
using System.Collections.Generic;
using System.IO;

namespace Hill
{
    class Program
    {
        static void Main()
        {
            var list = new List<double>();
            var sw = new StreamWriter(Console.OpenStandardOutput()) {AutoFlush = false};
            string tmp;
            Console.SetOut(sw);

            while (!String.IsNullOrEmpty(tmp = Console.ReadLine()))
            {
                var str = tmp.Split(' ');
                list.Add(Math.Log10(double.Parse(str[0]) + double.Parse(str[1])));
            }

            foreach (double t in list)
            {
                Console.WriteLine((int)t + 1);
            }
            Console.Out.Flush();
        }
    }
}