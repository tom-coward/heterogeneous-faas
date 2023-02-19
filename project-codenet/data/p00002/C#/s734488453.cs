using System;
using System.Text;
namespace AIZU_Cs_
{
    class Program
    {
        static void Main(string[] args)
        {
            int x,y;
            while (true)
            {
                string s = Console.ReadLine();
                if (s == null) break;
                args = s.Split(' ');
                x = int.Parse(args[0]);
                y = int.Parse(args[1]);
                x += y;
                Console.WriteLine(x.ToString().Length);
            }
        }
    }
}