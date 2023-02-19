using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AOJ
{
	class Program
	{
		static void Main(string[] args)
		{
			var str = "";
			while ((str = Console.ReadLine()) != null)
			{
				var ab = str.Split(' ').Select(i => int.Parse(i)).ToArray();
				Console.WriteLine((ab[0] + ab[1]).ToString().Length);
			}
		}
	}
}