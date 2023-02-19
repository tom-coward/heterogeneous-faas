using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Contest
{
    class Program
    {
        static int Main(string[] args)
        {
            new Program().Process();

            return 0;
        }

        private void Process()
        {
            int a, b;
            string s;
            char[] o = new char[1] { ' ' };

            s = Console.ReadLine();
            while (s != "" && s != "\n" && s != "\r\n" && s != null)
            {
                a = int.Parse(s.Split(o, StringSplitOptions.RemoveEmptyEntries)[0]);
                b = int.Parse(s.Split(o, StringSplitOptions.RemoveEmptyEntries)[1]);

                Console.WriteLine("{0}", (a + b).ToString().Length);
                s = Console.ReadLine();
            }
        }
    }
}