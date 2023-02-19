using System;

namespace hellounihara
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			for (int i = 0; i < 200; i++) {
			int a = 0;
			string arr02 =Console.ReadLine();
				if (arr02 == null) {
					break;
				}
		    string[] starr02 = arr02.Split();
			foreach (var item in starr02) {
				a += int.Parse(item);
			}
			int digit = a.ToString().Length;
			Console.WriteLine(digit);

			}
		}
	}
}