using System;

namespace Application
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			while(true) {
			   	var a=Console.ReadLine();
				if(a==null) break;
				var aa=a.Split();
				int sum=int.Parse (aa[0])+int.Parse(aa[1]);
				int ans=sum.ToString ().Length;
				Console.WriteLine ("{0}",ans);
			}
		}
	}
}
