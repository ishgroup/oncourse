#troubleshooting

Sometimes the server-integrations stuck for a long while > 30 min. Usualy because test db connection closed unexpectedly.

`java.net.SocketException: Socket closed
`
To restart action - select it in actions list

https://github.com/ishgroup/oncourse/actions/workflows/server_test.yml

Press 'Cancel workflow'
Then (after action really stopped) open menue and select rerun all jobs