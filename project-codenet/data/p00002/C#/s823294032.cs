using System;
using System.Linq;
using System.Text;

class Program
{
    static void Main()
    {
        string s;
        StringBuilder sb = new StringBuilder();
        while ((s = Console.ReadLine()) != null)
        {
            int ans = s.Split().Select(int.Parse).Sum().ToString().Count();
            sb.AppendLine(ans.ToString());
        }
        Console.Write(sb);
    }
}
