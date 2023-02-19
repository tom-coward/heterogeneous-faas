using System;
using System.Collections.Generic;

namespace Volume0_0002
{
	class Program
	{
		static void Main(string[] args)
		{
			List<Tuple<int, int>> ts = new List<Tuple<int, int>>();

			for(int i = 0; i < 200; i++) {
				string str = Console.ReadLine();
				if(str == null) {
					break;
				}

				string[] vs = str.Split(' ');
				ts.Add(Tuple.Create(int.Parse(vs[0]), int.Parse(vs[1])));
			}

			foreach(Tuple<int, int> t in ts) {
				string a = (t.Item1 + t.Item2).ToString();

				Console.WriteLine(a.Length);
			}
		}
	}
}

