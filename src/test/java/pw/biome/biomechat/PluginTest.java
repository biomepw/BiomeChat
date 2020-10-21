package pw.biome.biomechat;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pw.biome.biomechat.obj.ScoreboardHook;

public class PluginTest implements ScoreboardHook {
    private ServerMock server;
    private BiomeChat plugin;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BiomeChat.class);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testScoreboardHookRegistration() {
        Assert.assertEquals(plugin.getScoreboardHookList().size(), 0);

        plugin.registerHook(this);
        Assert.assertEquals(plugin.getScoreboardHookList().size(), 1);

        plugin.unregisterHook(this);
        Assert.assertEquals(plugin.getScoreboardHookList().size(), 0);
    }

    @Override
    public void restartScoreboardTask() {

    }

    @Override
    public void stopScoreboardTask() {

    }
}
