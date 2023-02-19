using System;
using System.Collections.Generic;

class MainClass
{
	public static void Main (string[] args)
	{
		string str;
		List<int> digit = new List<int> ();
		while (!string.IsNullOrEmpty(str = Console.ReadLine())) {
			string[] input = str.Split(' ');
			int a = int.Parse(input[0]);
			int b = int.Parse(input[1]);
			int c = a + b;
			digit.Add ((int)Math.Floor(Math.Log10(c)+1));
		}
	
	        for (int i = 0; i < digit.Count; i++) {
			Console.WriteLine (digit[i]);
		}
		Console.Read ();
	}
}