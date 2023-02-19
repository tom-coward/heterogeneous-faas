using System;
using System.Text;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;



class Program
{
    public const int INF = 1 << 25;

    static void Main()
    {
		string str;
		while (!string.IsNullOrEmpty(str = Console.ReadLine())) {
			string[] input = str.Split(' ');
			int a = int.Parse(input[0]);
			int b = int.Parse(input[1]);
			int c = a + b;
			int ans = 1;
			while (c / 10 != 0) {
				c /= 10;
				ans++;
			}
			Console.WriteLine(ans);
		}
	}
}