using System;

class Program
{
    static void Main()
    {
        while (true)
        {
            string str = Console.ReadLine();
            if (str == null)
                break;
            string[] s = str.Split(' ');
            int a = int.Parse(s[0]);
            int b = int.Parse(s[1]);
            int sum = a + b;
            string strsum = sum.ToString();
            Console.WriteLine("{0}", strsum.Length);
        }
    }

}