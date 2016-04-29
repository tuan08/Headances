package com.hazelcast.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.AtomicNumber;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.MultiMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "node-client-applicationContext-hazelcast.xml" })
public class TestClientApplicationContext {

	@Resource
	private HazelcastClient client;

	@Resource(name="instance")
	private HazelcastInstance instance;

	@Resource(name="map1")
	private IMap<Object, Object> map1;

	@Resource(name="map2")
	private IMap<Object, Object> map2;

	@Resource(name="multiMap")
	private MultiMap multiMap;

	@Resource(name="queue")
	private IQueue queue;

	@Resource(name="topic")
	private ITopic topic;

	@Resource(name="set")
	private ISet set;

	@Resource(name="list")
	private IList list;

	@Resource(name="executorService")
	private ExecutorService executorService;

	@Resource(name="idGenerator")
	private IdGenerator idGenerator;

	@Resource(name="atomicNumber")
	private AtomicNumber atomicNumber;

	@BeforeClass
	@AfterClass
	public static void start(){
		Hazelcast.shutdownAll();
	}

	@Test
	public void testClient() {
		assertNotNull(client);
		final IMap<Object, Object> map = client.getMap("default");
		map.put("Q", "q");
		final IMap<Object, Object> map2 = instance.getMap("default");
		assertEquals("q", map2.get("Q"));
	}

	@Test
	public void testHazelcastInstances() {
		assertNotNull(map1);
		assertNotNull(map2);

		assertNotNull(multiMap);
		assertNotNull(queue);
		assertNotNull(topic);
		assertNotNull(set);
		assertNotNull(list);
		assertNotNull(executorService);
		assertNotNull(idGenerator);
		assertNotNull(atomicNumber);

		assertEquals("map1", map1.getName());
		assertEquals("map2", map2.getName());

		assertEquals("multiMap", multiMap.getName());
		assertEquals("queue", queue.getName());
		assertEquals("topic", topic.getName());
		assertEquals("set", set.getName());
		assertEquals("list", list.getName());
		assertEquals("idGenerator", idGenerator.getName());
		assertEquals("atomicNumber", atomicNumber.getName());
	}
}
