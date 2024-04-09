import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * 시작: 23:05
 * 종료:
 * 
 * 문제해석
 * P명의 산타
 * 1. NxN 게임판, (1,1)~
 * 	M개의 턴, 매턴마다 루돌프와 산타들이 한번씩 움직임
 * 	루돌프가 1회 움직임, 1~P번 산타가 순서대로 움직임
 * 	기절 또는 격자밖으로 나가서 탈락한 산타는 움직임X
 * 	게임판 두칸 거리는 (r1-r2)^2+(c1-c2)^2
 * 2. 루돌프의 움직임
 * 	루돌프는 가장 가까운 산타를 향해 1칸 돌진
 * 	단, 게임에서 탈락하지 않은 산타 중 가까운 산타 선택
 * 		가까운 산타 2명이상 -> r -> c 우선순위
 * 	루돌프는 상하좌우,대각선 8방중 하나로 돌진 -> 대각선도 1칸임
 * 	가장 우선순위가 높은 산타를 향해 8방중 가까워지는 방향으로 1칸 돌진
 * 3. 산타 움직임
 * 	1~P번 순으로 움직임
 * 	기절 또는 이미 게임에서 탈락한 산타는 움직임X
 * 	산타는 루돌프에게 가장 가까워지는 방향으로 1칸 이동
 * 	산타는 다른 산타가 있는 칸 또는 게임판 밖으로 이동X
 * 	움직일 수 있는 칸이 없으면 이동X
 * 	움직일 수 있는 칸이 있어도 루돌프한테 가까워지는 방법 없으면 움직임X
 *  산타는 상하좌우 4방중 한곳으로 움직임
 *  	가까워지는 방향 여러개 -> 상우하좌 순
 * 4. 충돌
 * 	산타-루돌프 같은 칸에 있으면 충돌 발생
 * 	루돌프가 움직여서 충돌 : 산타는 C만큼의 점수+루돌프가 이동해온 방향으로 C칸 밀려남
 * 	산타가 움직여서 충돌 : 산타는 D만큼의 점수+산타가이동해온 반대방향으로 D칸 밀려남
 * 		포물선 모양을 그리며 밀려남, 이동하는 도중 충돌X->정확히 그 위치에 도달
 * 						밀려난 위치가 게임판 밖이면 산타 탈락
 * 						밀려난 위치에 다른산타가 있으면 상호작용발생
 * 5. 상호작용
 * 	루돌프와 충돌 후 착지하는 칸에서만 상호작용 발생
 * 	산타는 충돌 후 착지하게 되는 칸에 다른 산타가 있으면, 그 산타는 1칸 해당 방향으로 밀려남
 * 		그 옆에 산타가 또 있으면 연쇄적으로 1칸씩 밀려나는것 반복, 게임판 밖으로 밀려나온 산타는 게임에서 탈락
 * 6. 기절
 * 	산타는 루돌프와 충돌 후 기절
 * 	k번째 턴이면 k+1까지 기절->k+2부터 정상상태
 * 	기절한 산타는 움직임x, 기절한 도중 충돌이나 상호작용으로 인해 밀려날 수 있음
 * 루돌프는 기절한 산타를 돌진 대상으로 선택 가능
 * 7. 게임 종료
 * 	M번의 턴에 걸쳐 루돌프, 산타가 순서대로 움직인 이후 게임 종료
 * 	만약 P명의 산타가 모두 게임에서 탈락하면 게임 종료
 * 	매턴 이후 아직 탈락하지 않은 산타들에게는 1점씩 추가부여
 * 
 * 게임 종료 후 각 산타가 얻은 최종 점수 출력
 * 
 * 입력
 * 첫째줄: N게임판크기,M게임턴수,P산타수,C루돌프힘,D산타힘
 * 둘째줄: 루돌프 초기위치 r,c
 * P개줄: 산타번호+초기위치r,c
 * 처음 산타와 루돌프의 위치는 겹쳐져 주어지지 않음
 * 
 * 
 * 출력
 * 게임 종료 후 각 산타가 얻은 최종 점수 출력
 * 
 * 
 * 문제해결프로세스
 * 0. int point[], boolean out[]
 * 1. rubolmove()
 * 	산타거리 계산pq
 * 		우선순위: 거리작은->r큰->c큰
 *  	산타와 가까워지는 방향 선택 (8방)
 *  		crash(루)+기절time
 * 2. santamove()
 * 	1~P번 순 움직임
 * 		기절했으면 안움직임(t가 time-2보다크면)
 * 	루돌프와 최소거리로 1칸 이동(4방)
 * 		다른산타,격자밖 이동X
 * 		루돌프로부터 안가까워지면 이동X pq
 * 		 방법이 여러개면 상우하좌 우선순위
 * 		crash(산)+기절time
 * 3. crash()+기절time
 * 	산타-루돌프 같은 칸에 있으면 호출
 * 		루: 해당 산타점수+=C, 루돌프가 온 방향으로 C칸밀려남
 * 		산: 해당 산타점수+=D, 산타가 온 반대방향으로 D칸 밀려남
 * 	정확히 C,D칸 밀린 자리
 * 		게임판밖 -> outsanta=true
 * 		다른산타X -> santa 위치 r,c 조정
 * 		다른산타O -> 상호작용
 * 4. push()
 * 	착지 위치 산타 q에 넣음
 * 		while(!q.isempty())
 * 	1칸 밀려난 산타 또있으면 q에 넣음
 * 	1칸 밀려난 산타 게임판 밖이면 게임에서 탈락
 * 5. 게임종료?
 * 	P명이 모두 탈락시
 *  
 * 
 * 시간복잡도
 * 
 */
public class Main {
	static int N,M,P,C,D;
	static int point[];
	static boolean out[];
	static int map[][];
	static int rx,ry,time;
	static List<Santa> santa = new ArrayList<>();
	static int dx[] = {-1,-1,0,1,1,1,0,-1};
	static int dy[] = {0,1,1,1,0,-1,-1,-1};
	static class Santa implements Comparable<Santa>{
		int number,x,y,time;
		public Santa(int number, int x, int y, int time) {
			super();
			this.number = number;
			this.x = x;
			this.y = y;
			this.time = time;
		}
		@Override
		public int compareTo(Santa o) {
			return this.number-o.number;
		}
		@Override
		public String toString() {
			return "Santa [number=" + number + ", x=" + x + ", y=" + y + ", time=" + time + "]";
		}
		
	}
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());
		st = new StringTokenizer(br.readLine());
		rx = Integer.parseInt(st.nextToken())-1;
		ry = Integer.parseInt(st.nextToken())-1;
		point = new int[P+1];
		out = new boolean[P+1];
		map = new int[N][N];
		for (int i = 0; i < P; i++) {
			st = new StringTokenizer(br.readLine());
			int number = Integer.parseInt(st.nextToken());
			int x = Integer.parseInt(st.nextToken())-1;
			int y = Integer.parseInt(st.nextToken())-1;
			santa.add(new Santa(number,x,y,0));
			map[x][y] = number;
		}
		Collections.sort(santa);
		for (int i = 1; i <= M; i++) {
			time = i;
			rudolmove();
			santamove();
			if(endgame()) break;
			//else
			addpoint();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= P; i++) {
			sb.append(point[i]).append(' ');
		}
		System.out.println(sb);
		
	}
	public static void addpoint() {
		for (int i = 0; i < P; i++) {
			if(out[i+1]) continue;
			//else
			point[i+1]++;
		}
	}
	public static boolean endgame() {
		int cnt=0;
		for (int i = 1; i <= P; i++) {
			if(out[i]) cnt++;
		}
		if(cnt==P) return true;
		return false;
	}
	/* 문제해결프로세스
	 * 0. int point[], boolean out[]
	 * 1. rubolmove()
	 * 	산타거리 계산pq
	 * 		우선순위: 거리작은->r큰->c큰
	 *  	산타와 가까워지는 방향 선택 (8방)
	 *  		crash(루)+기절time
	 * 2. santamove()
	 * 	1~P번 순 움직임
	 * 		기절했으면 안움직임(t가 time-2보다크면)
	 * 	루돌프와 최소거리로 1칸 이동(4방)
	 * 		다른산타,격자밖 이동X
	 * 		루돌프로부터 안가까워지면 이동X pq
	 * 		 방법이 여러개면 상우하좌 우선순위
	 * 		crash(산)+기절time
	 * 3. crash()+기절time
	 * 	산타-루돌프 같은 칸에 있으면 호출
	 * 		루: 해당 산타점수+=C, 루돌프가 온 방향으로 C칸밀려남
	 * 		산: 해당 산타점수+=D, 산타가 온 반대방향으로 D칸 밀려남
	 * 	정확히 C,D칸 밀린 자리
	 * 		게임판밖 -> outsanta=true
	 * 		다른산타X -> santa 위치 r,c 조정
	 * 		다른산타O -> 상호작용
	 * 4. push()
	 * 	착지 위치 산타 q에 넣음
	 * 		while(!q.isempty())
	 * 	1칸 밀려난 산타 또있으면 q에 넣음
	 * 	1칸 밀려난 산타 게임판 밖이면 게임에서 탈락
	 * 5. 게임종료?
	 * 	P명이 모두 탈락시
	 */
	public static void santamove() {
		for (int i = 0; i < P; i++) {
			Santa s = santa.get(i);
			if(out[i+1] || s.time>time) continue;
			int origin = (rx-s.x)*(rx-s.x)+(ry-s.y)*(ry-s.y);
			int idx = -1;
			int min = Integer.MAX_VALUE;
			for (int k = 0; k < 8; k+=2) {
				int nx = s.x+dx[k];
				int ny = s.y+dy[k];
				int dis = (nx-rx)*(nx-rx)+(ny-ry)*(ny-ry);
				if(!rangecheck(nx,ny) || dis>=origin || map[nx][ny]>0) continue;
				if(dis<min) {
					idx=k;
					min = dis;
				}
			}
			//이동방향 존재
			if(idx!=-1) {
				int nx = s.x+dx[idx];
				int ny = s.y+dy[idx];
				map[s.x][s.y]=0;
				map[nx][ny]=s.number;
				santa.set(i, new Santa(s.number, nx, ny, s.time));
				//루돌프 충돌
				if(rx==nx && ry==ny) {
					idx -= 4;
					if(idx<0) idx+=8;
					map[nx][ny]=0;
					crush(s.number, idx, D);
				}
				
			}
		}
	}
	public static void crush(int num, int dir, int score) {
		point[num]+=score;
		
		Santa st = santa.get(num-1);
		int sx = st.x+score*dx[dir];
		int sy = st.y+score*dy[dir];
		//산타 밖으로 나감
		if(!rangecheck(sx,sy)) {
			out[num] = true;
			map[rx][ry]=0;
			return;
		}
		//else
		//다른 산타 존재
		if(map[sx][sy]>0) {
			push(sx, sy, dir);
		}
		//안전 착지
		//else
		map[sx][sy]=num;
		santa.set(num-1, new Santa(st.number, sx, sy,time+2));
		map[rx][ry]=0;
	}
	
	public static void push(int sx, int sy, int dir) {
		Queue<Santa> q = new ArrayDeque<>();
		q.offer(santa.get(map[sx][sy]-1));
		
		while(!q.isEmpty()) {
			Santa s = q.poll();
			int x = s.x;
			int y = s.y;
			int num = s.number;
			
			int nx = x+dx[dir];
			int ny = y+dy[dir];
			//산타 밖으로 나감
			if(!rangecheck(nx,ny)) {
				out[num] = true;
				continue;
			}
			//else
			//다른 산타 존재
			if(map[nx][ny]>0) {
				q.offer(santa.get(map[nx][ny]-1));
			}
			//안전 착지
			//else 
			map[nx][ny]=num;
			santa.set(num-1, new Santa(num, nx, ny, s.time));
		}
	}
	
	static class Distance implements Comparable<Distance>{
		Santa s;
		int distance;
		public Distance(Santa s, int distance) {
			super();
			this.s = s;
			this.distance = distance;
		}
		@Override
		public int compareTo(Distance o) {
			if(this.distance==o.distance) {
				if(this.s.x==o.s.x) {
					return o.s.y-this.s.y;
				}
				return o.s.x-this.s.x;
			}
			//else
			return this.distance-o.distance;
		}
		@Override
		public String toString() {
			return "Distance [s=" + s + ", distance=" + distance + "]";
		}
		
	}
	
	public static void rudolmove() {
		PriorityQueue<Distance> pq = new PriorityQueue<>();
		for (int i = 0; i < P; i++) {
			Santa s = santa.get(i);
			if(out[i+1]) continue;
			int dis = (rx-s.x)*(rx-s.x)+(ry-s.y)*(ry-s.y);
			pq.add(new Distance(s,dis));
		}
		//가장 가까운 산타
		Distance distance = pq.poll();
		Santa s = distance.s;
		int sx = s.x;
		int sy = s.y;
		int min = Integer.MAX_VALUE;
		int idx = -1;
		for (int i = 0; i < 8; i++) {
			int nx = rx+dx[i];
			int ny = ry+dy[i];
			int dis = (nx-sx)*(nx-sx)+(ny-sy)*(ny-sy);
			if(!rangecheck(nx,ny)) continue;
			if(dis<min) {
				idx=i;
				min = dis;
			}
		}
		rx +=dx[idx];
		ry +=dy[idx];
		//산타 존재 -> 충돌
		if(map[rx][ry]>0) {
			crush(map[rx][ry], idx, C);
		}
		//else
		//안전 이동
	}

	
	
	public static boolean rangecheck(int cx, int cy) {
		return cx>=0 && cx<N && cy>=0 && cy<N;
	}

}