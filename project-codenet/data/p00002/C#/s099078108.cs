using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using System.IO;
class ListOfTopThreeHills
{
	static void Main(string[] args)
	{
		do
		{
			string input = Console.ReadLine();
			if (string.IsNullOrEmpty(input))
			{
				break;
			}
			string[] inputArr = input.Split(' ');
			int a = int.Parse(inputArr[0]);
			int b = int.Parse(inputArr[1]);
			int sum = a + b;
			Console.WriteLine(sum.ToString().Length);
		} while (true);
	}
}