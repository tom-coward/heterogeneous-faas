using System;

namespace sample1
{
	class MainClass
	{
		public static void Main(string[] args)
		{
			String str = Console.ReadLine();
			while (str != null)
			{
				String[] strs = str.Split();
				var sum = int.Parse(strs[0]) + int.Parse(strs[1]);
				Console.WriteLine((int)Math.Log10(sum) + 1);
				str = Console.ReadLine();
			}
		}
	}
}

