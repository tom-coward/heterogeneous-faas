using System;

class MyClass
{
    static void Main(string[] args)
    {
        string str = "";
        while ((str = Console.ReadLine()) != null)
        {
            string[] str2 = str.Split(' ');
            int a = int.Parse(str2[0]);
            int b = int.Parse(str2[1]);
            Console.WriteLine((int)Math.Floor(Math.Log10(a + b)) + 1);
        }
    }
}
