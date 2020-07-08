# CloudEvents Processing

## Try it out

This image is available on Quay.io:

```shell script
$ podman run --rm -it -p 8080:8080 quay.io/ricardozanini/ce-processing:1.0
```

Then just `curl` the 8080 port:

```shell script
$ curl -X POST \
    -H "content-type: application/json"  \
    -H "ce-specversion: 1.0"  \
    -H "ce-source: curl-command"  \
    -H "ce-type: curl.demo"  \
    -H "ce-id: 123-abc"  \
    -d '{"name":"Zanini"}' \
http://localhost:8080
```

You should see the application logs like:

```shell script
19:55:11.701 [vert.x-eventloop-thread-1] INFO  o.m.c.ce.CloudEventListenerVerticle - Initializing Vertx Server
19:55:11.826 [vert.x-eventloop-thread-2] INFO  o.m.c.ce.CloudEventPublisherVerticle - CE Publisher started, will publish events to 'http://localhost:8080/' endpoint
19:55:11.828 [vert.x-eventloop-thread-0] INFO  org.m88i.cloud.ce.MainVerticle - Successfully deployed MainVerticle
19:55:11.832 [vert.x-eventloop-thread-1] INFO  o.m.c.ce.CloudEventListenerVerticle - Server started on port 8080
Jul 08, 2020 7:55:11 PM io.vertx.core.impl.launcher.commands.VertxIsolatedDeployer
INFO: Succeeded in deploying verticle



19:55:17.477 [vert.x-eventloop-thread-1] INFO  o.m.c.ce.CloudEventListenerVerticle - Received event: CloudEvent{id='123-abc', source=curl-command, type='curl.demo', datacontenttype='application/json', dataschema=null, subject='null', time=null, data=[123, 34, 110, 97, 109, 101, 34, 58, 34, 68, 97, 118, 101, 34, 125], extensions={}}
19:55:17.499 [vert.x-eventloop-thread-2] INFO  o.m.c.ce.CloudEventPublisherVerticle - Received internal message. Creating new CE request
19:55:17.503 [vert.x-eventloop-thread-2] INFO  o.m.c.ce.CloudEventPublisherVerticle - CE created CloudEvent{id='9ec36d7d-6eda-4db3-a00c-8e3b896fb420', source=5a522904f683, type='success.ce.demo', datacontenttype='null', dataschema=null, subject='null', time=null, data=[67, 69, 32, 80, 114, 111, 99, 101, 115, 115, 101, 100, 32, 73, 68, 58, 32, 49, 50, 51, 45, 97, 98, 99], extensions={}}, sending
19:55:17.547 [vert.x-eventloop-thread-1] INFO  o.m.c.ce.CloudEventListenerVerticle - Received event: CloudEvent{id='9ec36d7d-6eda-4db3-a00c-8e3b896fb420', source=5a522904f683, type='success.ce.demo', datacontenttype='null', dataschema=null, subject='null', time=null, data=[67, 69, 32, 80, 114, 111, 99, 101, 115, 115, 101, 100, 32, 73, 68, 58, 32, 49, 50, 51, 45, 97, 98, 99], extensions={}}
19:55:17.548 [vert.x-eventloop-thread-1] INFO  o.m.c.ce.CloudEventListenerVerticle - Won't emit a follow up CloudEvent since CloudEvent{id='9ec36d7d-6eda-4db3-a00c-8e3b896fb420', source=5a522904f683, type='success.ce.demo', datacontenttype='null', dataschema=null, subject='null', time=null, data=[67, 69, 32, 80, 114, 111, 99, 101, 115, 115, 101, 100, 32, 73, 68, 58, 32, 49, 50, 51, 45, 97, 98, 99], extensions={}} comes from a local source
```
