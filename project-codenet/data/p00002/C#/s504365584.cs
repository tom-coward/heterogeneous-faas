using System;
using System.Collections.Generic;

namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {

            List<int> hList=new List<int>();

            string s="";


            int dat = 0;

            while(true)
            {
                s = Console.ReadLine();

                if (s == null)
                {
                    break;
                }

                dat = Convert.ToInt32(s.Split(' ')[0]) + Convert.ToInt32(s.Split(' ')[1]);

                hList.Add(dat.ToString().Length);
                
            } 


            for (int i = 0; i < hList.Count; i++)
            {
                Console.WriteLine(hList[i].ToString());
            }
            Console.ReadLine(); 
        }
    }

}