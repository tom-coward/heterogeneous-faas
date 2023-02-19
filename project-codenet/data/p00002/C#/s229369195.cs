using System;
class wa
{
    static void Main()
    {
        int[] a = new int[200];
        int r=0;
        for(int i = 0; i <200; i++)
        {
            string b = Console.ReadLine();
            if (string.IsNullOrEmpty(b)) { break; }
            string[] c = b.Split(' ');
            long d = long.Parse(c[0]), e = long.Parse(c[1]);
            long f = d + e;
            b = f.ToString();
            a[i] = b.Length;
            r++;
        }
        for(int i = 0; i < r; i++)
        {
            Console.WriteLine(a[i]);
        }
        
    }
}