using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AOJ
{
    class DigitNumber
    {
        static void Main(string[] args)
        {
            while(true)
            {
                string line = System.Console.ReadLine();
                if(line=="" || line==null)
                {
                    return;
                }
                else
                {
                    string[] param = line.Split(' ');
                    System.Console.WriteLine((ulong.Parse(param[0]) + ulong.Parse(param[1])).ToString().Length);

                }
            }
        }
    }
}