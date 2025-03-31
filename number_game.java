import java.util.*;
class number_game{
    public static void main(String ar[]){
        Scanner sc=new Scanner (System.in);
	Random rand = new Random();
	int total_points=0;
	System.out.println("Welcome to Guess a Number Challenge!!\nRules:\n1. You have to correctly guess a number randomly generated by the system in a range of 1 to 100.\n2. You will be given three rounds to guess the correct number\n3. Incase you guess almost near to the actual number then you will be given a bonus of two rounds\n");
	System.out.println("Are you ready?  1='Yes' 0='No'");
	int check=sc.nextInt();
	while (check!=0)
	{
		int r=rand.nextInt(101);
		int points=0,rounds=0,bonus=0;
		while (rounds!=3+bonus)
		{
			System.out.println("\nEnter a number between 1 and 100");
			int n=sc.nextInt();
			if (n==r)
			{
				points+=100;
				System.out.println("Correct Guess!! the number is "+r);

			}
			else if ((n<=r+5 && n>r) || (n>=r-5 && n<r))
			{
				System.out.println("Almost near!!");
				bonus=2;
			}
			else if (n>r+5)
			{
				System.out.println("Too High");
			}
			else if (n<r-5)
			{
				System.out.println("Too Low");
			}
			rounds+=1;
			if (points==100)
			{
				break;
			}
		}
		if (points==100)
		{
			System.out.println("\nYou Won!!");
			if (rounds==1)
			{
				total_points+=(points+100);
			}
			else if (rounds==2)
			{
				total_points+=(points+80);
			}
			else if (rounds==3)
			{
				total_points+=(points+60);
			}
			else if (rounds==4)
			{
				total_points+=(points+40);
			}
			else if (rounds==5)
			{
				total_points+=(points+20);
			}

			System.out.println("Points Scored = "+total_points);
		}
		else
		{
			System.out.println("\nGame Over!! Too many failed attempts");
			System.out.println("The actual number was "+r);
			System.out.println("Points Scored = "+total_points);
		}
		System.out.println("\nDo you want to continue this game?  1='Yes' 0='No'");
		check=sc.nextInt();
	}
    }
}