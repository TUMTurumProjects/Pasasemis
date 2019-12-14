import org.junit.Test;
import testee.pgdp.collections.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * A test class containing hopefully enough tests for HA-0803
 *
 * @author Maxi Barmetler
 * @version 1.1
 */
public class w08h03
{
	private final static boolean USE_RANDOM_SEED = false;
	private final static long RANDOM_SEED = new Date().getTime();
	private final static Random RANDOM = USE_RANDOM_SEED ? new Random(RANDOM_SEED) : new Random(1337);

	private static String createRandomString(int size)
	{
		String result = "";
		for (int j = 0; j < 20; ++j)
			result += Math.random() < 0.5 ?
					"" + ((char) (Math.random() * 27) + 'a') :
					((char) (Math.random() * 27) + 'A');
		return result;
	}

	private static String wrongSize(int expected, int actual)
	{
		return "Wrong size [expected: " + expected + ", was: " + actual + "]";
	}

	private static String wrongValue(Object expected, Object actual)
	{
		return "Wrong value [expected: " + expected.toString() + ", was: " + actual.toString() + "]";
	}

	@Test public void testList()
	{
		String[] elements = new String[50];
		for (int i = 0; i < elements.length; ++i)
			elements[i] = createRandomString(20);

		List<String> list = new List<>(elements[0]);
		List<String> curr = list;
		for (int i = 1; i < elements.length; ++i)
		{
			curr.insert(elements[i]);
			assertNotNull(curr.getNext());
			curr = curr.getNext();
		}

		assertEquals(elements.length, list.length());

		curr = list;

		for (String element : elements)
		{
			assertNotNull(curr);
			assertEquals(element, curr.getInfo());
			curr = curr.getNext();
		}
	}

	@Test public void testLinkedStack()
	{
		String[] elements = new String[50];
		for (int i = 0; i < elements.length; ++i)
			elements[i] = createRandomString(20);

		Stack<String> stack = new LinkedStack<>();
		for (int i = elements.length - 1; i >= 0; --i)
			stack.push(elements[i]);

		assertEquals(wrongSize(elements.length, stack.size()), elements.length, stack.size());

		for (String element : elements)
		{
			String s = stack.pop();
			assertEquals(wrongValue(element, s), element, s);
		}
	}

	@Test public void testLinkedQueue()
	{
		String[] elements = new String[50];
		for (int i = 0; i < elements.length; ++i)
			elements[i] = createRandomString(20);

		Queue<String> queue = new LinkedQueue<>();
		for (int i = 0; i < elements.length; ++i)
			queue.enqueue(elements[i]);

		assertEquals(wrongSize(elements.length, queue.size()), elements.length, queue.size());

		for (String element : elements)
		{
			String s = queue.dequeue();
			assertEquals(wrongValue(element, s), element, s);
		}
	}

	@Test public void testDataStructureLinkStackToQueue()
	{
		java.util.List<String> la = new ArrayList<>();
		java.util.List<String> lb = new ArrayList<>();

		for (int i = 0; i < 50; ++i)
		{
			la.add(createRandomString(20));
			if (i < 15)
				lb.add(createRandomString(20));
		}

		Stack<String> a = new LinkedStack<>();
		Queue<String> b = new LinkedQueue<>();

		for (String e : la)
			a.push(e);
		for (String e : lb)
			b.enqueue(e);

		assertEquals("(a) " + wrongSize(la.size(), a.size()), la.size(), a.size());
		assertEquals("(b) " + wrongSize(lb.size(), b.size()), lb.size(), b.size());

		for (int i = la.size() - 1; i >= 0; --i)
			lb.add(la.get(i));

		DataStructureLink<String> link = new DataStructureLink<>(new StackConnector<>(a), new QueueConnector<>(b));
		link.moveAllFromAToB();

		assertEquals("(b) " + wrongSize(lb.size(), b.size()), lb.size(), b.size());

		for (String expected : lb)
		{
			String actual = b.dequeue();
			assertEquals(wrongValue(expected, actual), expected, actual);
		}
	}

	@Test public void testPenguinCustomerValidInput()
	{
		String name = createRandomString(20);
		int money = RANDOM.nextInt(100);
		PenguinCustomer customer = new PenguinCustomer(name, money);

		assertEquals(wrongValue(name, customer.getName()), name, customer.getName());
		assertEquals(wrongValue(money, customer.getMoney()), money, customer.getMoney());

		int amount = (int) (Math.random() * money) + 1;
		customer.pay(amount);

		assertEquals(wrongValue(money - amount, customer.getMoney()), money - amount, customer.getMoney());
	}

	@Test(expected = RuntimeException.class) public void testPenguinCustomerInValidInput1()
	{
		PenguinCustomer customer = new PenguinCustomer(null, 10);
	}

	@Test(expected = RuntimeException.class) public void testPenguinCustomerInValidInput2()
	{
		PenguinCustomer customer = new PenguinCustomer("asd", -1);
	}

	@Test(expected = RuntimeException.class) public void testPenguinCustomerInValidInput3()
	{
		int money = RANDOM.nextInt(80) + 20;
		PenguinCustomer customer = new PenguinCustomer("asd", money);
		customer.pay(money + 1);
	}

	@Test public void testPenguinCustomerBand()
	{
		PenguinCustomer customer = new PenguinCustomer("asd", 10);
		Queue<FishyProduct> a = new LinkedQueue<>();
		Queue<FishyProduct> b = new LinkedQueue<>();

		FishyProduct[] products = new FishyProduct[50];
		FishyProduct[] extraProducts = new FishyProduct[50];

		for (int i = 0; i < products.length; ++i)
		{
			products[i] = new FishyProduct(createRandomString(20), RANDOM.nextInt(9) + 2);
			extraProducts[i] = new FishyProduct(createRandomString(20), RANDOM.nextInt(9) + 2);
			a.enqueue(products[i]);
		}

		customer.takeAllProductsFromBand(a);
		for (FishyProduct fp : extraProducts)
			customer.addProductToBasket(fp);

		customer.placeAllProductsOnBand(b);

		FishyProduct[] expected = new FishyProduct[products.length + extraProducts.length];
		for (int i = 0; i < expected.length; ++i)
			expected[expected.length - i - 1] = i < products.length ? products[i] : extraProducts[i - products.length];

		assertEquals(wrongSize(0, a.size()), 0, a.size());
		assertEquals(wrongSize(expected.length, b.size()), expected.length, b.size());

		for (FishyProduct fp : expected)
		{
			FishyProduct actual = b.dequeue();
			assertSame("Wrong element after Cashier", fp, actual);
		}
	}

	@Test public void testCheckout()
	{
		final int nCustomers = 10;

		Checkout checkout = new Checkout();

		PenguinCustomer[] customers = new PenguinCustomer[nCustomers];
		FishyProduct[][] products = new FishyProduct[nCustomers][];
		int[] money = new int[nCustomers];
		int[] costs = new int[nCustomers];
		for (int i = 0; i < nCustomers; ++i)
		{
			money[i] = RANDOM.nextInt(1000) + 500;
			costs[i] = 0;
			customers[i] = new PenguinCustomer(createRandomString(20), money[i]);
			products[i] = new FishyProduct[RANDOM.nextInt(10) + 10];
			for (int j = 0; j < products[i].length; ++j)
			{
				products[i][j] = new FishyProduct(createRandomString(20), RANDOM.nextInt(20) + 10);
				customers[i].addProductToBasket(products[i][j]);
				costs[i] += products[i][j].getPrice();
			}
			assertEquals(wrongSize(products[i].length, customers[i].getProducts().size()), products[i].length,
					customers[i].getProducts().size());
			checkout.getQueue().enqueue(customers[i]);
		}

		assertEquals(wrongSize(nCustomers, checkout.queueLength()), nCustomers, checkout.queueLength());

		for (int i = 0; i < nCustomers; ++i)
		{
			checkout.serveNextCustomer();

			assertEquals(wrongValue(money[i] - costs[i], customers[i].getMoney()), money[i] - costs[i],
					customers[i].getMoney());

			assertEquals(wrongSize(products[i].length, customers[i].getProducts().size()), products[i].length,
					customers[i].getProducts().size());

			for (int j = 0; j < products[i].length; ++j)
			{
				FishyProduct actual = customers[i].getProducts().pop();
				assertSame("Wrong element after Cashier", products[i][j], actual);
			}
		}
	}

	@Test(expected = RuntimeException.class) public void testPenguinSuperMarketIllegal1()
	{
		new PenguinSupermarket(0);
	}

	@Test public void testPenguinSuperMarket()
	{
		final int nCustomers = 123;

		int nCheckouts = 7;
		PenguinSupermarket sm = new PenguinSupermarket(nCheckouts);

		PenguinCustomer[] customers = new PenguinCustomer[nCustomers];
		FishyProduct[][] products = new FishyProduct[nCustomers][];
		int[] money = new int[nCustomers];
		int[] costs = new int[nCustomers];
		for (int i = 0; i < nCustomers; ++i)
		{
			money[i] = RANDOM.nextInt(1000) + 500;
			costs[i] = 0;
			customers[i] = new PenguinCustomer(createRandomString(20), money[i]);
			products[i] = new FishyProduct[RANDOM.nextInt(10) + 10];
			for (int j = 0; j < products[i].length; ++j)
			{
				products[i][j] = new FishyProduct(createRandomString(20), RANDOM.nextInt(20) + 10);
				customers[i].addProductToBasket(products[i][j]);
				costs[i] += products[i][j].getPrice();
			}
			assertEquals(wrongSize(products[i].length, customers[i].getProducts().size()), products[i].length,
					customers[i].getProducts().size());
		}

		for (int i = 0; i < nCustomers; ++i)
		{
			customers[i].goToCheckout(sm);
			if (i % Math.max((int) (nCheckouts * 0.8), 1) != 0 && i != nCustomers - 1)
				continue;

			int[] customerAmounts = new int[nCheckouts];
			for (int j = 0; j < nCheckouts; ++j)
				customerAmounts[j] = sm.getCheckouts()[j].queueLength();

			sm.serveCustomers();

			for (int j = 0; j < nCheckouts; ++j)
				assertEquals(wrongSize(Math.max(customerAmounts[j] - 1, 0), sm.getCheckouts()[j].queueLength()),
						Math.max(customerAmounts[j] - 1, 0), sm.getCheckouts()[j].queueLength());
		}

		for (int i = 0; i < nCustomers; ++i)
		{
			assertEquals(wrongValue(money[i] - costs[i], customers[i].getMoney()), money[i] - costs[i],
					customers[i].getMoney());

			assertEquals(wrongSize(products[i].length, customers[i].getProducts().size()), products[i].length,
					customers[i].getProducts().size());

			for (int j = 0; j < products[i].length; ++j)
			{
				FishyProduct actual = customers[i].getProducts().pop();
				assertSame("Wrong element after Cashier", products[i][j], actual);
			}
		}
	}

	@Test(expected = RuntimeException.class) public void testPenguinSuperMarketCloseIllegal1()
	{
		PenguinSupermarket sm = new PenguinSupermarket(1);
		sm.closeCheckout(0);
	}

	@Test(expected = RuntimeException.class) public void testPenguinSuperMarketCloseIllegal2()
	{
		PenguinSupermarket sm = new PenguinSupermarket(10);
		sm.closeCheckout(10);
	}

	@Test(expected = RuntimeException.class) public void testPenguinSuperMarketCloseIllegal3()
	{
		PenguinSupermarket sm = new PenguinSupermarket(10);
		sm.closeCheckout(-1);
	}

	@Test public void testPenguinSuperMarketCloseLegal()
	{
		final int nCustomers = 10;
		final int nCheckouts = 5;
		final int close = 1;

		PenguinSupermarket sm = new PenguinSupermarket(nCheckouts);
		PenguinCustomer[] customers = new PenguinCustomer[nCustomers];
		for (int i = 0; i < nCustomers; ++i)
		{
			customers[i] = new PenguinCustomer(createRandomString(20), 10);
			customers[i].goToCheckout(sm);
		}

		PenguinCustomer[][] queues = new PenguinCustomer[nCheckouts][nCustomers];

		int[] len = new int[nCheckouts];
		for (int i = 0; i < nCheckouts; ++i)
			len[i] = 0;

		for (int cus = 0, che = 0; cus < nCustomers; ++cus)
		{
			queues[che][len[che]] = customers[cus];
			++len[che];
			che = (che + 1) % nCheckouts;
		}

		for (int i = 0; i < nCheckouts - 1; ++i)
			assertEquals(wrongSize(len[i], sm.getCheckouts()[i].queueLength()), len[i],
					sm.getCheckouts()[i].queueLength());

		for (int i = len[close] - 1; i >= 0; --i)
		{
			int minLen = Integer.MAX_VALUE;
			int minInd = 0;
			for (int j = 0; j < nCheckouts; ++j)
			{
				if (j != close && len[j] < minLen)
				{
					minLen = len[j];
					minInd = j;
				}
			}

			queues[minInd][len[minInd]++] = queues[close][--len[close]];
			queues[close][len[close]] = null;
		}

		sm.closeCheckout(close);

		for (int i = 0, j = 0; i < nCheckouts - 1; ++i, ++j)
		{
			if (j == close)
				++j;
			assertEquals(wrongSize(len[j], sm.getCheckouts()[i].queueLength()), len[j],
					sm.getCheckouts()[i].queueLength());
			for (int p = 0; p < len[j]; ++p)
				assertSame("Wrong penguin", queues[j][p], sm.getCheckouts()[i].getQueue().dequeue());
		}
	}
}
