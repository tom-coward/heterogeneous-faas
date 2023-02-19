using System;
class main
{
    static void Main(string[] args)
    {
        while (true)
        {
            string s = Console.ReadLine();
            if (s != null)
            {
                    string[] t = s.Split(' ');
                    int a = int.Parse(t[0]);
                    int b = int.Parse(t[1]);
                    int c = a + b;
 
                    Console.WriteLine(c.ToString().Length);
            }
            if (s == null)
            {
                break;
            }
        }
    }
}