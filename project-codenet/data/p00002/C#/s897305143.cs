using System;

namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {
            while (true)
            {
                string a = Console.ReadLine();
                if (a == null)
                {
                    break;
                }
                else
                {
                    if (a.Length == 0)
                        continue;
                    string[] str = a.Split(' ');
                    

                    int n = Convert.ToInt32(str[0]);
                    int m = Convert.ToInt32(str[1]);
                    int p = n + m;
                    string wr = Convert.ToString(p);
                    int w = wr.Length;
                    Console.WriteLine(w);
                }
            }
        }
    }
}