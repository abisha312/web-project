import java.util.*;
class ATM
{
	public int acc_balance = 1000000;
 	public int withdraw(int amount)
	{
		if (acc_balance-1000>=amount)
		{
			acc_balance-=amount;
			return 1;
		}
		else
		{
			return 0;
		}
	}
	public void deposit(int amount)
	{
		acc_balance+=amount;
	}
	public void checkBalance()
	{
		System.out.println(acc_balance);
	}
}
class atm_interface
{
    public static void main(String ar[])
    {	
        Scanner sc=new Scanner (System.in);
	System.out.println("Welcome to MMM Bank");
	ATM atm = new ATM();
	int pin = 9876, c = 0, b = 1;
	while (c<3)
	{
		if (c!=0)
		{
			System.out.println("Try Again");
		}
		System.out.println("\nEnter your pin");
		int user_pin = sc.nextInt();
		if (user_pin==pin)
		{
			while (b==1)
			{
				System.out.println("\nEnter any one of the options:\n1.Withdrawal\n2.Deposit\n3.Account Balance");
				int get = sc.nextInt();
				while (get!=1 && get!=2 && get!=3)
				{
					System.out.println("\nEnter any one of the options:\n1.Withdrawal\n2.Deposit\n3.Account Balance");
					get = sc.nextInt();
				}
				if (get==1)
				{
					System.out.println("\nEnter the amount to be withdrawed");
					int amount = sc.nextInt();
		 			int success = atm.withdraw(amount);
					if (success==1)
					{
						System.out.println("\nCollect Your Cash");
					}
					else
					{
						System.out.println("\nWithdrawal Unsuccessful");
					}
				}
				else if (get==2)
				{
					System.out.println("\nEnter the amount to be deposited");
					int amount = sc.nextInt();
		 			atm.deposit(amount);
					System.out.println("\nDeposition Successful");
				}
				else if (get==3)
				{
					System.out.println("\nYour Account Balance is:");
		 			atm.checkBalance();
				}
				System.out.println("\nDo you want to continue banking?  1='Yes' 0='No'");
				b=sc.nextInt();
				c=4;
			}
		}
		else
		{
			System.out.println("\nYou Enterd the wrong pin");
			c+=1;
		}
	}
	if (c==3)
	{
		System.out.println("Too many failed attempts, Your account has been temporarily closed");
	}
	else
	{
		System.out.println("Happy Banking :)");

	}
    }
}