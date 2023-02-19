using System;

namespace AOJ_Progran
{
    class Program
    {
        static void Main(string[] args)
        {
            bool x = true;
            while(x)
            {
                string str = Console.ReadLine();
                if (str == null)
                    break;
                string[] s = str.Split(' ');
                int a = int.Parse(s[0]);
                int b = int.Parse(s[1]);
                int num = a+b;
                string strNum = num.ToString();
                
                Console.WriteLine("{0}",strNum.Length);
            }
        }
    }
}