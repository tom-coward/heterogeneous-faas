using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ConsoleApplication1
{   
    class Program
    {
           static void Main(string[] args) {
            while(true)
            {

                string sss = Console.ReadLine();
                if (sss== null)
                    return;
                int[] sss2 = sss.Split().Select(int.Parse).ToArray();
                int  a = sss2[0]+sss2[1];  string b = a.ToString();      Console.WriteLine("{0}",b.Length);

            }
        }
    }
    }