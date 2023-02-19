using System;
using System.Linq;

namespace Test06
{
	class MainClass
	{
		public static void Main(string[] args)
		{
			string strOfnum;
			while ((strOfnum = Console.ReadLine()) != null)
			{
				string[] abStr = strOfnum.Split();
				int sum = int.Parse(abStr[0]) + int.Parse(abStr[1]);
				char[] count = sum.ToString().ToCharArray();
				Console.WriteLine(count.Length);

			}
		}
	}
}